<?xml version="1.0" encoding="UTF-8"?>
<!--
 | Copyright (c) 2012 Symplectic Limited. All rights reserved.
 | This Source Code Form is subject to the terms of the Mozilla Public
 | License, v. 2.0. If a copy of the MPL was not distributed with this
 | file, You can obtain one at http://mozilla.org/MPL/2.0/.
 -->
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                xmlns:bibo="http://purl.org/ontology/bibo/"
                xmlns:vivo="http://vivoweb.org/ontology/core#"
                xmlns:foaf="http://xmlns.com/foaf/0.1/"
                xmlns:score="http://vivoweb.org/ontology/score#"
                xmlns:ufVivo="http://vivo.ufl.edu/ontology/vivo-ufl/"
                xmlns:vitro="http://vitro.mannlib.cornell.edu/ns/vitro/0.7#"
                xmlns:api="http://www.symplectic.co.uk/publications/api"
                xmlns:symp="http://www.symplectic.co.uk/vivo/"
                xmlns:svfn="http://www.symplectic.co.uk/vivo/namespaces/functions"
                xmlns:config="http://www.symplectic.co.uk/vivo/namespaces/config"
                exclude-result-prefixes="rdf rdfs bibo vivo foaf score ufVivo vitro api symp svfn config xs"
        >

    <xsl:import href="elements-to-vivo-utils.xsl" />

    <xsl:template match="api:object[@category='user']">
        <xsl:call-template name="_render_rdf_object">
            <xsl:with-param name="objectURI" select="svfn:userURI(.)" />
            <xsl:with-param name="rdfNodes">
                <rdf:type rdf:resource="http://xmlns.com/foaf/0.1/Person" />
                <rdfs:label>
                    <xsl:value-of select="api:last-name" />, <xsl:value-of select="api:first-name" />
                </rdfs:label>
                <vivo:preferredTitle><xsl:value-of select="api:title" /></vivo:preferredTitle>
                <vivo:primaryEmail><xsl:value-of select="api:email-address" /></vivo:primaryEmail>
                <foaf:lastName><xsl:value-of select="api:last-name" /></foaf:lastName>
                <foaf:firstName><xsl:value-of select="api:first-name" /></foaf:firstName>
                <rdf:type rdf:resource="http://vivoweb.org/harvester/excludeEntity" />
                <rdf:type rdf:resource="http://vitro.mannlib.cornell.edu/ns/vitro/0.7#Flag1Value1Thing" />
                <!-- rdf:type rdf:resource="http://www.symplectic.co.uk/vivo/User" / -->
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
</xsl:stylesheet>