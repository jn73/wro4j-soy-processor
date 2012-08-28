package se.oobo.wro.soy;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;

import com.google.common.io.InputSupplier;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jssrc.SoyJsSrcOptions;

public class SoyPreProcessor implements ResourcePreProcessor {

	private PropertiesConfiguration configuration;

	public SoyPreProcessor() {
		try {
			configuration = new PropertiesConfiguration("wro-soy.properties");
		} catch (ConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public void process(Resource resource, final Reader reader, Writer writer)
			throws IOException {

		SoyFileSet sfs = new SoyFileSet.Builder().add(
				new InputSupplier<Reader>() {

					public Reader getInput() throws IOException {
						return reader;
					}
				}, resource.getUri()).build();

		try {
			List<String> jsSources = sfs.compileToJsSrc(new SoyJsSrcOptions(),
					null);
			writer.write(jsSources.get(0));
			writer.close();
		} catch (Exception e) {
			writer.write("Failed to render soy file: " + resource.getUri()
					+ "\n" + e.toString() + "\n\n");
		}
	}

}
