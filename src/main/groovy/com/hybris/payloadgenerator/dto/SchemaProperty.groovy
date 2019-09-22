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

class SchemaProperty {
	String name
	String type
	// will be used for determining mandatory attributes
	boolean isMandatory

	private SchemaProperty() {}

	static final class Builder {
		String name
		String type
		boolean isMandatory

		private Builder() {}

		static Builder property() { return new Builder() }

		Builder withName(String propertyName) {
			name = propertyName
			this
		}

		Builder withType(String propertyType) {
			type = propertyType
			this
		}

		Builder isMandatory(boolean isPropertyUnique) {
			isMandatory = isPropertyUnique
			this
		}

		SchemaProperty build() {
			SchemaProperty property = new SchemaProperty()
			property.setName(name)
			property.setType(type)
			property.setIsMandatory(isMandatory)
			property
		}
	}

	@Override
	String toString() {
		return "SchemaProperty{" +
				"name='" + name + '\'' +
				", type='" + type + '\'' +
				", isMandatory=" + isMandatory +
				'}'
	}
}
