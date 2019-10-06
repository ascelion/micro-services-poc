package ascelion.micro.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class AbstractDockerTask extends DefaultTask {
	final String operation
	final List<String> options = []
	final List<String> arguments = []

	boolean ignoreFailures

	final String tool

	AbstractDockerTask(String tool, String operation) {
		this.tool = tool
		this.operation = operation
	}

	DockerExtension getExtension() {
		project.extensions.docker
	}

	def getTouchFile() {
		new File( extension.outputDirectory, '.docker-' + operation )
	}

	@TaskAction
	void execute() {
		project.exec {
			workingDir = extension.outputDirectory
			executable = tool
			ignoreExitValue = ignoreFailures

			args += options
			args operation
			args += arguments
		}
	}
}
