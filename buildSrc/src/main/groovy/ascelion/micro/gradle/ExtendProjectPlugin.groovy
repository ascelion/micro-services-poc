package ascelion.micro.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class ExtendProjectPlugin implements Plugin<Project> {

	@Override
	void apply(Project project) {
		project.metaClass.stringProperty = { String name, String defVal ->
			project.ext.properties.getOrDefault( name, defVal )
		}
		project.metaClass.booleanProperty = { String name, boolean defVal ->
			String prp = project.stringProperty( name, Boolean.toString(defVal) )

			return prp.empty || Boolean.valueOf( prp )
		}
		project.metaClass.integerProperty = { String name, int defVal ->
			return Integer.valueOf( project.stringProperty( name, Integer.toString(defVal) ) )
		}

		project.metaClass.stringProperty = { String name ->
			project.stringProperty( name, null )
		}
		project.metaClass.booleanProperty = { String name ->
			project.booleanProperty( name, false )
		}
		project.metaClass.integerProperty = { String name ->
			project.integerProperty( name, 0 )
		}
	}
}
