package ascelion.micro.gradle

class StackUpTask extends DockerTask {
	StackUpTask() {
		super("stack")
	}

	@Override
	public void execute() {
		arguments << 'up'
		arguments << '-c'
		arguments << 'docker-compose.yml'

		arguments << extension.name

		super.execute()
	}
}
