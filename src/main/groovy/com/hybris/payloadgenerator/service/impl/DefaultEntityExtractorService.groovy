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
import com.hybris.payloadgenerator.dto.SchemaProperty
import com.hybris.payloadgenerator.service.EntityExtractorService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

import javax.xml.parsers.DocumentBuilderFactory

@Component
class DefaultEntityExtractorService implements EntityExtractorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEntityExtractorService)
	private static final String ENTITY_TYPES = 'EntityType'
	private static final String NAME = 'Name'
	private static final String TYPE = 'Type'
	private static final String UNIQUE = 's:IsUnique'
	public static final String PROPERTY = 'Property'
	public static final String NAVIGATION_PROPERTY = 'NavigationProperty'
	public static final String TO_ROLE = 'ToRole'


	@Override
	EdmxSchema extractSchema(InputStream edmxStream) {
		Document document = getXmlDocument(edmxStream)
		NodeList nodes = document.getElementsByTagName(ENTITY_TYPES)
		LOGGER.debug("Found {} types in EDMX schema", nodes.length)

		List<Entity> entities = new ArrayList<>(nodes.length)
		for (int i = 0; i < nodes.length; i++) {
			def entityName = nodes.item(i).getAttributes().getNamedItem(NAME).getNodeValue()
			LOGGER.debug("Found entity type with name: {}", entityName)
			def node = nodes.item i
			if (node.nodeType == Node.ELEMENT_NODE) {
				Element element = (Element) node
				def standardProps = getPropertiesFrom element, PROPERTY
				def navigationProps = getPropertiesFrom element, NAVIGATION_PROPERTY
				entities.add Entity.Builder.anEntity()
						.withName(entityName)
						.withProperties(standardProps)
						.withNavProperties(navigationProps)
						.build()
			}
		}
		new EdmxSchema(entities)
	}

	private List<SchemaProperty> getPropertiesFrom(Element element, String propertyType) {
		def props = []
		def SchemaProperties = element.getElementsByTagName(propertyType)
		for (int j = 0; j < SchemaProperties.length; j++) {
			def propName = SchemaProperties.item(j).getAttributes().getNamedItem(NAME).getNodeValue()
			def propType = SchemaProperties.item(j).getAttributes().getNamedItem(propertyType == PROPERTY ? TYPE : TO_ROLE).getNodeValue()
			def isUnique = SchemaProperties.item(j).getAttributes().getNamedItem(UNIQUE)
			def isMandatory = isUnique != null ? Boolean.valueOf(isUnique.getNodeValue()) : false
			def prop = SchemaProperty.Builder.property()
					.withName(propName)
					.withType(propType)
					.isMandatory(isMandatory)
					.build()
			LOGGER.debug('Found {}: {}', propertyType, prop)
			props.add prop
		}
		props
	}

	private Document getXmlDocument(InputStream edmxStream) {
		def docFactory = DocumentBuilderFactory.newInstance()
		def docBuilder = docFactory.newDocumentBuilder()
		def document = docBuilder.parse(edmxStream)
		document.getDocumentElement().normalize()
		document
	}
}
