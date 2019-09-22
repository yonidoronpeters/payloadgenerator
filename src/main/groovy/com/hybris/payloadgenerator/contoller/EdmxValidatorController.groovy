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

package com.hybris.payloadgenerator.contoller

import com.hybris.payloadgenerator.dto.EdmxForm
import com.hybris.payloadgenerator.service.EntityExtractorService
import org.apache.commons.io.IOUtils
import org.apache.olingo.odata2.api.exception.ODataException
import org.apache.olingo.odata2.core.edm.provider.EdmxProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

import static java.nio.charset.StandardCharsets.UTF_8

@Controller
class EdmxValidatorController {
	private static final Logger LOGGER = LoggerFactory.getLogger(EdmxValidatorController)
	private EntityExtractorService entityExtractorService

	EdmxValidatorController(EntityExtractorService entityExtractorService) {
		this.entityExtractorService = entityExtractorService
	}

	@GetMapping('/')
	def validateEdmx(final Model model) {
		LOGGER.info('Rendering form for EDMX validation')
		model.addAttribute('edmxForm', new EdmxForm())
		'validateSchema'
	}

	@PostMapping('/validateEdmx')
	def edmxSubmit(EdmxForm edmx, @RequestParam("file") MultipartFile file, Model model) {
		LOGGER.debug('Validating schema...')
		def stream
		try {
			if (file.getSize() > 0) {
				stream = file.getInputStream()
			} else {
				stream = IOUtils.toInputStream(edmx.getEdmxSchema(), UTF_8)
			}
			new EdmxProvider().parse(stream, true)
			LOGGER.debug('Schema is valid')
			stream.reset()
			def schema = entityExtractorService.extractSchema(stream)
			model.addAttribute('entityTypes', schema)
			'validSchema'
		} catch (final ODataException e) {
			LOGGER.error("The EDMX schema is invalid", e)
			model.addAttribute('error', e)
			'invalidSchema'
		}
	}
}
