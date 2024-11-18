package org.springframework.integration.junit;

import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;

public class ResourceHolder implements CloseableResource {

    private final Object resource;

    public ResourceHolder(Object resource) {
        this.resource = resource;
    }

    public Object getResource() {
        return resource;
    }

    @Override
    public void close() throws Exception {
        if (resource instanceof AutoCloseable closeable) {
            closeable.close();
        }
    }
}
