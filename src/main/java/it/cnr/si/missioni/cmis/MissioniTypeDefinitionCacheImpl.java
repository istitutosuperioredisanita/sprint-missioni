package it.cnr.si.missioni.cmis;

import org.apache.chemistry.opencmis.client.bindings.cache.TypeDefinitionCache;
import org.apache.chemistry.opencmis.client.bindings.spi.BindingSession;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MissioniTypeDefinitionCacheImpl implements TypeDefinitionCache {
    private transient static final Log logger = LogFactory.getLog(MissioniTypeDefinitionCacheImpl.class);
    @Override
    public void initialize(BindingSession session) {
        logger.debug("Create MissioniTypeDefinitionCacheImpl");
    }

    @Override
    public void put(String repositoryId, TypeDefinition typeDefinition) {
        MissioniCMISService.CACHE_TYPES.put(typeDefinition.getId(), typeDefinition);
    }

    @Override
    public TypeDefinition get(String repositoryId, String typeId) {
        return MissioniCMISService.CACHE_TYPES.get(typeId);
    }

    @Override
    public void remove(String repositoryId, String typeId) {
        MissioniCMISService.CACHE_TYPES.remove(typeId);
    }

    @Override
    public void remove(String repositoryId) {
        //NOT IMPLEMENTED
    }

    @Override
    public void removeAll() {
        //NOT CLEAR CACHES TYPES
    }
}
