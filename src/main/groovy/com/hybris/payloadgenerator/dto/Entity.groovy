/*
 * [y] hybris Platform
 *
 * Copyright (c) 2019 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package com.hybris.payloadgenerator.dto

class Entity {
	String name
	List<SchemaProperty> navProperties
	List<SchemaProperty> standardProperties

	private Entity() {}

	static final class Builder {
		String name
		List<SchemaProperty> navProperties
		List<SchemaProperty> standardProperties

		private Builder() {}

		static Builder anEntity() { return new Builder() }

		Builder withName(String entityName) {
			name = entityName
			this
		}

		 Builder withNavProperties(List<SchemaProperty> navigationProperties) {
			 navProperties = navigationProperties
			 this
		 }

		Builder withProperties(List<SchemaProperty> properties) {
			standardProperties = properties
			this
		}

		Entity build() {
			Entity entity = new Entity()
			entity.setName(name)
			entity.setNavProperties(navProperties)
			entity.setStandardProperties(standardProperties)
			entity
		}
	}
}
