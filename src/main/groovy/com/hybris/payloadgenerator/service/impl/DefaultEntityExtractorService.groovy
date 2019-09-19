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

package com.hybris.payloadgenerator.service.impl

import com.hybris.payloadgenerator.dto.EdmxSchema
import com.hybris.payloadgenerator.dto.Entity
import com.hybris.payloadgenerator.service.EntityExtractorService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.w3c.dom.Document
import org.w3c.dom.NodeList

import javax.xml.parsers.DocumentBuilderFactory

@Component
class DefaultEntityExtractorService implements EntityExtractorService{

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEntityExtractorService)
	private static final String ENTITY_TYPES = "EntityType"
	public static final String NAME = "Name"


	@Override
	EdmxSchema extractSchema(InputStream edmxStream) {
		Document document = getXmlDocument(edmxStream)
		NodeList nodes = document.getElementsByTagName(ENTITY_TYPES)
		LOGGER.debug("Found {} types in EDMX schema", nodes.length)

		List<Entity> entities = new ArrayList<>(nodes.length)
		for (int i = 0; i < nodes.length; i++) {
			def entityName = nodes.item(i).getAttributes().getNamedItem(NAME).getNodeValue()
			LOGGER.debug("Found entity type with name: {}", entityName)
			entities.add(new Entity(entityName))
			// TODO populate Entity with regular and nav properties
		}
		new EdmxSchema(entities)
	}

	private Document getXmlDocument(InputStream edmxStream) {
		def docFactory = DocumentBuilderFactory.newInstance()
		def docBuilder = docFactory.newDocumentBuilder()
		def document = docBuilder.parse(edmxStream)
		document.getDocumentElement().normalize()
		document
	}
}
