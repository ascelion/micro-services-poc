package ascelion.micro.gradle

abstract class DockerTask extends AbstractDockerTask {
	DockerTask(String operation) {
		super('docker', operation)
	}
}
