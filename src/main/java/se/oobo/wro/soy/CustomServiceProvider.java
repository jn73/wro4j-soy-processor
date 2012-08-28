package se.oobo.wro.soy;

import java.util.HashMap;
import java.util.Map;

import ro.isdc.wro.model.resource.processor.ResourcePostProcessor;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;
import ro.isdc.wro.model.resource.processor.support.ProcessorProvider;

public class CustomServiceProvider implements ProcessorProvider {

	public Map<String, ResourcePreProcessor> providePreProcessors() {
		Map<String, ResourcePreProcessor> providers = new HashMap<String, ResourcePreProcessor>();
		providers.put("soy", new SoyPreProcessor());
		return providers;
	}

	public Map<String, ResourcePostProcessor> providePostProcessors() {
		return null;
	}

}
