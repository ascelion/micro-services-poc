package ascelion.micro.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin

class DockerPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		def ext = project.extensions.create("docker", DockerExtension, project)

		project.tasks.register('dockerFiles', FreemarkerTask ) {
			group = 'docker'
			description = 'Creates Docker files'
		}
		project.tasks.register('dockerTag', TagTask ) {
		}
		project.tasks.register('dockerBuild', BuildTask ) {
			group = 'docker'
			description = 'Build Docker services'

			dependsOn 'dockerFiles'

			project.plugins.withType( ApplicationPlugin ) {
				dependsOn 'assemble'
			}

			finalizedBy 'dockerTag'
		}
		project.tasks.register('dockerUp', UpTask ) {
			group = 'docker'
			description = 'Create and start Docker containers (use --no-start to avoid starting services)'

			dependsOn 'dockerBuild'
		}
		project.tasks.register('dockerStart', StartTask ) {
			group = 'docker'
			description = 'Start Docker services'

			dependsOn 'dockerBuild'
		}

		project.tasks.register('dockerDown', DownTask ) {
			group = 'docker'
			description = 'Stop and remove containers and volumes'

			dependsOn 'dockerFiles'
		}
		project.tasks.register('dockerStop', StopTask ) {
			group = 'docker'
			description = 'Stop Docker services'

			dependsOn 'dockerFiles'
		}

		project.tasks.register('dockerPush', PushTask ) {
			group = 'docker'
			description = 'Push service images'

			dependsOn 'dockerBuild'
		}
		project.tasks.register('dockerStackUp', StackUpTask ) {
			group = 'docker'
			description = 'Push service images'

			dependsOn 'dockerBuild'
		}

		project.tasks.register('checkDevProfile', CheckDevProfileTask)
	}
}
