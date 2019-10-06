package ascelion.micro.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.yaml.snakeyaml.Yaml

class CheckDevProfileTask extends DefaultTask {

	@TaskAction
	void execute() {
		def docker = project.extensions.docker.configuration
		def devFile = project.file("src/main/resources/application-dev.yml")

		if( devFile.exists() ) {
			def dev = new Yaml().load(devFile.newReader())

			assert "${docker.portBase + 8080}" == "${dev.server.port}"

			if( docker.hasDatabase ) {
				assert "${docker.portBase + 5432}" == "${dev.database.port}"
			}
		}
	}
}
