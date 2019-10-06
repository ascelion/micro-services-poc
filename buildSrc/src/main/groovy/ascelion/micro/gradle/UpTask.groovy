package ascelion.micro.gradle

import org.gradle.api.tasks.options.Option

class UpTask extends DockerComposeTask {

	@Option(option="no-start", description="Avoid starting services")
	boolean no_start

	@Option(description="Scale services")
	List<String> scale = []

	UpTask() {
		super("up")
	}

	@Override
	public void execute() {
		if( no_start ) {
			arguments << '--no-start'
		}
		else {
			arguments << '--detach'
		}

		if( extension.scalable ) {
			scale.each {
				arguments << '--scale'
				arguments << (it.contains('=') ? it : "$project.name=$it")
			}
		}

		super.execute()
	}
}
