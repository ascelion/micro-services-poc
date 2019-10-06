package ascelion.micro.gradle

import freemarker.template.Configuration
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

@CacheableTask
class FreemarkerTask extends DefaultTask {
	private final Configuration config = new Configuration(Configuration.VERSION_2_3_29)

	FreemarkerTask() {
		config.directoryForTemplateLoading = project.rootDir
		config.booleanFormat = 'c'
		config.numberFormat = 'computer'
		config.tagSyntax = Configuration.AUTO_DETECT_TAG_SYNTAX
		config.interpolationSyntax = Configuration.SQUARE_BRACKET_INTERPOLATION_SYNTAX
	}

	DockerExtension getExtension() {
		project.extensions.docker
	}

	@Input
	def getConfiguration() {
		extension.configuration
	}

	@InputFiles
	def getTemplates() {
		extension.templates
	}

	def getOutputDirectory() {
		extension.outputDirectory
	}

	@OutputFiles
	def getOutputFiles() {
		def files = []

		templates.each { files << destinationFile(it) }

		return files
	}

	@TaskAction
	void execute(IncrementalTaskInputs inputs) {
		if( inputs.incremental ) {
			inputs.outOfDate { processFile it.file }
		}
		else {
			templates
					.filter { it.file }
					.each { processFile it }
		}
	}

	void processFile( File file ) {
		config
				.getTemplate(project.rootDir.relativePath(file))
				.process(configuration.properties, destinationFile(file).newWriter())
	}

	File destinationFile( File file ) {
		return new File(outputDirectory, file.name)
	}
}
