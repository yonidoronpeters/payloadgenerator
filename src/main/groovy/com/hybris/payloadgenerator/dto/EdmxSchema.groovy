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

import java.util.stream.Collectors

class EdmxSchema {
	List<Entity> entityTypes

	EdmxSchema(final List<Entity> entities) {
		entityTypes = entities
	}

	List<String> getEntityNames() {
		entityTypes.stream()
		.map({ e -> e.name })
		.collect(Collectors.toList())
	}
}
