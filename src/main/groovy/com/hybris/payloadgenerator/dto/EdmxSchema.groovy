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

import org.w3c.dom.Document
import org.w3c.dom.Element

import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import java.util.stream.Collectors

class EdmxSchema {
	private static final String LOCALIZED = 'Localized___'
	List<Entity> entityTypes
	Map<String, String> typeToSamplePayload
	Map<String, String> typeToSamplePayloadWithOnlyMandatoryFields

	EdmxSchema(final List<Entity> entities) {
		entityTypes = entities
		typeToSamplePayload = generateSamplePayloads()
		typeToSamplePayloadWithOnlyMandatoryFields = generateSamplePayloads(false)
	}

	List<String> getEntityNames() {
		entityTypes.stream()
				.map({ e -> e.name })
				.filter({ name -> !name.startsWith(LOCALIZED) })
				.collect(Collectors.toList())
	}

	String generatePayloadForEntity(String entityName, boolean allFields) {
		def entity = entityContainedInSchema(entityName)
		if (entity.isPresent()) {
			return buildXml(entity.get(), allFields)
		}
		return "No such entity $entityName"
	}

	def generateSamplePayloads(boolean allFields = true) {
		getEntityNames().stream()
				.collect(Collectors.toMap({ entity -> entity.toString() }, { entity -> generatePayloadForEntity(entity, allFields) }))
	}

	private Optional<Entity> entityContainedInSchema(String entityName) {
		entityTypes.stream()
				.filter({ entity -> entity.name == entityName })
				.findAny()
	}

	private String buildXml(Entity entity, boolean allFields) {
		def dbFactory = DocumentBuilderFactory.newInstance()
		def dBuilder = dbFactory.newDocumentBuilder()
		def doc = dBuilder.newDocument()

		def rootElement = doc.createElement entity.name
		doc.appendChild rootElement

		appendProperties(doc, rootElement, entity, allFields)

		transformXmlToString(doc)
	}

	private String transformXmlToString(Document doc) {
		TransformerFactory transformerFactory = TransformerFactory.newInstance()
		Transformer transformer = transformerFactory.newTransformer()
		transformer.setOutputProperty(OutputKeys.INDENT, "yes")
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
		DOMSource source = new DOMSource(doc)
		StringWriter writer = new StringWriter()
		StreamResult result = new StreamResult(writer)
		transformer.transform(source, result)
		writer.toString()
	}

	private void appendProperties(Document doc, Element element, Entity entity, boolean allFields) {
		appendSimpleProps(doc, element, entity, allFields)
		appendNavigationProps(doc, element, entity, allFields)
	}

	/** Example simple properties on {@code CatalogVersion} entity type:
	 *             <CatalogVersion>
	 *               <version>Staged</version>
	 *               <integrationKey>Staged|Default</integrationKey>
	 *             </CatalogVersion>
	 * @param document the XML document to append to
	 * @param root the XML element to append the properties to
	 * @param entity the entity which simple properties are generated for
	 */
	private void appendSimpleProps(Document document, Element root, Entity entity, boolean allFields) {
		entity.standardProperties
				.forEach({ prop ->
					if (allFields || prop.isMandatory) {
						def element = document.createElement(prop.name)
						element.appendChild document.createTextNode(prop.type.replace('Edm.', ''))
						root.appendChild element
					}
				})
	}

	/** Example navigation property {@code Catalog} on {@code CatalogVersion} entity type
	 *             <CatalogVersion>
	 *               <catalog>
	 *                 <Catalog>
	 *                  ...
	 *                 </Catalog>
	 *               </catalog>
	 *              ...
	 *             </CatalogVersion>
	 * @param document the XML document to append to
	 * @param root the parent element, e.g. CatalogVersion in the example above
	 * @param entity the entity which navigation properties are generated for
	 */
	private void appendNavigationProps(Document document, Element root, Entity entity, boolean allFields) {
		entity.navProperties
				.forEach({ prop ->
					if (allFields || prop.isMandatory) {
						def propertyName = document.createElement(prop.name)
						def entityType = document.createElement(prop.type)
						propertyName.appendChild entityType
						root.appendChild propertyName
						appendProperties(document, entityType, entityTypes.find { e -> e.name == prop.type }, allFields)
					}
				})
	}

}
