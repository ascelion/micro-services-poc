package ascelion.micro.gradle

import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile

class BuildTask extends DockerComposeTask {
	BuildTask() {
		super("build")
	}

	@Override
	@OutputFile
	def getTouchFile() {
		super.getTouchFile()
	}

	@InputFiles
	def getInputFiles() {
		project.fileTree( extension.outputDirectory ) {
			exclude '.docker-*'
		}
	}

	@Override
	public void execute() {
		super.execute()

		project.ant {
			touch file: touchFile
		}
	}
}
