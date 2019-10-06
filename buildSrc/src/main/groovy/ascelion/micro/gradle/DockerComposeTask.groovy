package ascelion.micro.gradle

abstract class DockerComposeTask extends AbstractDockerTask {
	DockerComposeTask(String operation) {
		super('docker-compose', operation)
	}

	@Override
	public void execute() {
		options << '-p'
		options << extension.name

		super.execute()
	}
}
