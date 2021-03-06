package org.jvalue.ods.adapterservice.adapter.api.rest.v1;

import org.jvalue.ods.adapterservice.adapter.Adapter;
import org.jvalue.ods.adapterservice.adapter.model.AdapterConfig;
import org.jvalue.ods.adapterservice.adapter.model.DataBlob;
import org.jvalue.ods.adapterservice.adapter.model.ProtocolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/")
public class AdapterEndpoint {

  private final Adapter adapter;

  @Autowired
  public AdapterEndpoint(Adapter adapter) {
    this.adapter = adapter;
  }


  @PostMapping(value = Mappings.IMPORT_PATH, produces = "application/json")
  public ResponseEntity<?> executeDataImport(
    @Valid @RequestBody AdapterConfig config,
    @RequestParam(required = false) boolean includeData) {
    return this.executePreview(config, includeData);
  }

  @PostMapping(value = Mappings.PREVIEW_PATH, produces = "application/json")
  public ResponseEntity<?> executePreview(
    @Valid @RequestBody AdapterConfig config,
    @RequestParam(required = false, defaultValue = "true") boolean includeData) {
    try {
      DataBlob imported = adapter.executeJob(config);

      if (includeData) {
        return ResponseEntity.ok(imported);
      }

      return ResponseEntity.ok(imported.getMetaData());

    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @PostMapping(value = Mappings.RAW_PREVIEW_PATH, produces = "application/json")
  public ResponseEntity<?> executeRawPreview(
    @Valid @RequestBody ProtocolConfig config,
    @RequestParam(required = false, defaultValue = "true") boolean includeData) {
    try {
      DataBlob imported = adapter.executeRawImport(config);

      if (includeData) {
        return ResponseEntity.ok(imported);
      }

      return ResponseEntity.ok(imported.getMetaData());

    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }
}
