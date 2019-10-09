package ascelion.micro.gradle

import groovy.transform.PackageScope
import org.gradle.internal.metaobject.AbstractDynamicObject
import org.gradle.internal.metaobject.DynamicInvokeResult

class DockerConfiguration extends AbstractDynamicObject implements Serializable {
	static private final Set<String> RO_PROPERTIES = ['properties', 'name', 'scalable', 'tagName']

	private final Map<String, Object> properties = new HashMap<>()

	@Override
	public String getDisplayName() {
		return "Docker configuration"
	}

	@PackageScope
	void name(String name) {
		this.properties.put("name", name)
	}
	@PackageScope
	void scalable(boolean scalable) {
		this.properties.put("scalable", scalable)
	}
	@PackageScope
	void tagName(String tagName) {
		this.properties.put("tagName", tagName)
	}

	@Override
	public boolean hasProperty(String name) {
		if( "properties".equals(name) ) {
			return true
		}

		return this.properties.containsKey(name)
	}

	@Override
	public DynamicInvokeResult trySetProperty(String name, Object value) {
		if( RO_PROPERTIES.contains(name) ) {
			throw setReadOnlyProperty(name)
		}

		this.properties.put(name, value)

		return DynamicInvokeResult.found()
	}

	@Override
	public DynamicInvokeResult tryGetProperty(String name) {
		if( "properties".equals(name) ) {
			return DynamicInvokeResult.found(this.properties)
		}

		if (this.properties.containsKey(name)) {
			return DynamicInvokeResult.found(this.properties.get(name))
		}

		return DynamicInvokeResult.notFound()
	}
}
