package ascelion.micro.gradle

import org.gradle.api.tasks.options.Option

class DownTask extends DockerComposeTask {

	@Option(option = "volumes" , description="Remove volumes")
	boolean volumes

	DownTask() {
		super("down")

		ignoreFailures = true
	}

	@Override
	public void execute() {
		if( volumes ) {
			arguments << '--volumes'
		}

		super.execute()
	}
}
