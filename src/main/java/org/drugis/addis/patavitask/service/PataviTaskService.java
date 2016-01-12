package org.drugis.addis.patavitask.service;

import org.drugis.addis.exception.ResourceDoesNotExistException;
import org.drugis.addis.models.exceptions.InvalidModelTypeException;
import org.drugis.addis.patavitask.PataviTaskUriHolder;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by connor on 26-6-14.
 */
public interface PataviTaskService {
  PataviTaskUriHolder getPataviTaskUriHolder(Integer projectId, Integer analysisId, Integer modelId) throws ResourceDoesNotExistException, IOException, SQLException, InvalidModelTypeException;
}
