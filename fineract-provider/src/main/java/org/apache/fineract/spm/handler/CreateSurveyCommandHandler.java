package org.apache.fineract.spm.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.springframework.stereotype.Service;

@Service
@CommandType(entity = "SURVEY", action = "CREATE")
public interface CreateSurveyCommandHandler extends NewCommandSourceHandler {

}
