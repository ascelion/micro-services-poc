package ascelion.micro.gradle

class TagTask extends DockerTask {
	TagTask() {
		super('tag')

		onlyIf {
			extension.tagName != 'latest'
		}
	}

	@Override
	public void execute() {
		arguments << "${extension.repository}${extension.configuration.application.replace('-', '_')}:${extension.tagName}"
		arguments << "${extension.repository}${extension.configuration.application.replace('-', '_')}"

		super.execute()
	}
}


