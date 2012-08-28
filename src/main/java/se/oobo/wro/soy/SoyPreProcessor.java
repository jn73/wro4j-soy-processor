package se.oobo.wro.soy;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.processor.ResourcePreProcessor;

import com.google.common.io.InputSupplier;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.jssrc.SoyJsSrcOptions.CodeStyle;

public class SoyPreProcessor implements ResourcePreProcessor {

	private PropertiesConfiguration configuration;

	public SoyPreProcessor() {
		try {
			configuration = new PropertiesConfiguration("wro-soy.properties");
			configuration
					.setReloadingStrategy(new FileChangedReloadingStrategy());
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
			List<String> jsSources = sfs.compileToJsSrc(crateSoyJsOptions(),
					null);
			writer.write(jsSources.get(0));
			writer.close();
		} catch (Exception e) {
			writer.write("Failed to render soy file: " + resource.getUri()
					+ "\n" + e.toString() + "\n\n");
		}
	}

	private SoyJsSrcOptions crateSoyJsOptions() {
		SoyJsSrcOptions opts = new SoyJsSrcOptions();
		opts.setBidiGlobalDir(configuration.getInt("wro4j.soy.bidiGlobalDir"));
		opts.setCodeStyle(parseCodeStyle(configuration.getString("wro4j.soy.codestyle")));
		opts.setGoogMsgsAreExternal(configuration.getBoolean("wro4j.soy.googMsgsAreExternal"));
		opts.setShouldAllowDeprecatedSyntax(configuration.getBoolean("wro4j.soy.shouldAllowDeprecatedSyntax"));
		opts.setShouldDeclareTopLevelNamespaces(configuration.getBoolean("wro4j.soy.shouldDeclareTopLevelNamespaces"));
		opts.setShouldGenerateGoogMsgDefs(configuration.getBoolean("wro4j.soy.shouldGenerateGoogMsgDefs"));
		opts.setShouldGenerateJsdoc(configuration.getBoolean("wro4j.soy.shouldGenerateJsdoc"));
		opts.setShouldProvideRequireJsFunctions(configuration.getBoolean("wro4j.soy.shouldProvideRequireJsFunctions"));
		opts.setShouldProvideRequireSoyNamespaces(configuration
				.getBoolean("wro4j.soy.shouldProvideRequireSoyNamespaces"));
		return opts;
	}

	private CodeStyle parseCodeStyle(String style) {
		if ("concat".equalsIgnoreCase(style)) {
			return CodeStyle.CONCAT;
		} else if ("stringbuilder".equalsIgnoreCase(style)) {
			return CodeStyle.STRINGBUILDER;
		} else {
			throw new RuntimeException(
					"Unhandled parameter value for codestyle: " + style);
		}
	}

}
