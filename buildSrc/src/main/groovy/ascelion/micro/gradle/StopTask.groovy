package ascelion.micro.gradle

class StopTask extends DockerComposeTask {
	StopTask() {
		super("stop")

		ignoreFailures = true
	}
}
