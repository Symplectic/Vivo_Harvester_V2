/*******************************************************************************
 * Copyright (c) 2012 Symplectic Ltd. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package uk.co.symplectic.vivoweb.harvester.translate;

import org.apache.commons.lang.NullArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import uk.co.symplectic.vivoweb.harvester.model.ElementsItemId;
import uk.co.symplectic.vivoweb.harvester.model.ElementsItemInfo;
import uk.co.symplectic.vivoweb.harvester.model.ElementsRelationshipInfo;
import uk.co.symplectic.vivoweb.harvester.store.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class ElementsRelationshipTranslateObserver extends ElementsTranslateObserver{

    private static final Logger log = LoggerFactory.getLogger(ElementsRelationshipTranslateObserver.class);
    private final ElementsItemFileStore rawDataStore;

    public ElementsRelationshipTranslateObserver(ElementsItemFileStore rawDataStore, ElementsRdfStore rdfStore, String xslFilename){
        super(rdfStore, xslFilename, StorableResourceType.RAW_RELATIONSHIP);
        if(rawDataStore == null) throw new NullArgumentException("rawDataStore");
        this.rawDataStore = rawDataStore;
    }
    @Override
    protected void observeStoredRelationship(ElementsRelationshipInfo info, ElementsStoredItem item) {
        Map<String, Object> extraXSLTParameters = new HashMap<String, Object>();
        extraXSLTParameters.put("extraObjects", getExtraObjectsDescription(info));
        translate(item, extraXSLTParameters);
    }

    private Document getExtraObjectsDescription(ElementsRelationshipInfo info) {

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element mainDocRootElement = doc.createElement("extraObjects");
            doc.appendChild(mainDocRootElement);
            for (ElementsItemId.ObjectId id : info.getObjectIds()) {
                ElementsItemInfo storedItemInfo = ElementsItemInfo.createObjectItem(id.getCategory(), id.getId());
                BasicElementsStoredItem storedRawObject = rawDataStore.retrieveItem(storedItemInfo.getItemId(), StorableResourceType.RAW_OBJECT);
                if(storedRawObject != null) {
                    StoredData storedRawData = storedRawObject.getStoredData();
                    InputStream storedItemInputStream = null;
                    try {
                        storedItemInputStream = storedRawData.getInputStream();
                        Document storedObjectDoc = docBuilder.parse(storedRawData.getInputStream());
                        Element storedObjectRootElement = storedObjectDoc.getDocumentElement();
                        Node importedNode = doc.importNode(storedObjectRootElement, true);
                        mainDocRootElement.appendChild(importedNode);
                    } catch (FileNotFoundException fnfe) {
                        //todo: decide if this is desirable or not - needed to avoid failures in relation to data categories you are not really processing at the moment.
                        if (storedRawData instanceof StoredData.InFile) {
                            StoredData.InFile fileStoredData = (StoredData.InFile) storedRawData;
                            log.warn(MessageFormat.format("File {0} for extra object {1} not found when processing {2}", fileStoredData.getFile().getPath(), id, info.getItemId()));
                        }
                    } finally {
                        if (storedItemInputStream != null) {
                            storedItemInputStream.close();
                        }
                    }
                }
                else{
                    log.warn(MessageFormat.format("File for extra object {0} not found when processing {2}", id, info.getItemId()));
                }
            }
            return doc;
        }
        catch(IOException ioe){
            throw new IllegalStateException(ioe);
        }
        catch(SAXException se){
            throw new IllegalStateException(se);
        }
        catch (ParserConfigurationException pce) {
            throw new IllegalStateException(pce);
        }
    }
}
