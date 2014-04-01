package ch.hsr.faith.android.app.services;

import java.util.List;

import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.app.Application;

import com.octo.android.robospice.SpringAndroidSpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.springandroid.json.jackson.JacksonObjectPersisterFactory;

public class JSONService extends SpringAndroidSpiceService {

	public static final String SERVICE_BASE_URL_PROPERTY_KEY = "base_url";
	
	@Override
	public CacheManager createCacheManager(Application application) throws CacheCreationException {
		CacheManager cacheManager = new CacheManager();
		JacksonObjectPersisterFactory jacksonObjectPersisterFactory = new JacksonObjectPersisterFactory(application);
		cacheManager.addPersister(jacksonObjectPersisterFactory);
		return cacheManager;
	}

	@Override
	public RestTemplate createRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();

		// web services support json responses
		MappingJacksonHttpMessageConverter jsonConverter = new MappingJacksonHttpMessageConverter();
		FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
		final List<HttpMessageConverter<?>> listHttpMessageConverters = restTemplate.getMessageConverters();

		listHttpMessageConverters.add(jsonConverter);
		listHttpMessageConverters.add(formHttpMessageConverter);
		listHttpMessageConverters.add(stringHttpMessageConverter);
		restTemplate.setMessageConverters(listHttpMessageConverters);
		return restTemplate;
	}
}