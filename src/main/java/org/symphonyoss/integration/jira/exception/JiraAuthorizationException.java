/**
 * Copyright 2016-2017 Symphony Integrations - Symphony LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.symphonyoss.integration.jira.exception;

import org.symphonyoss.integration.exception.IntegrationRuntimeException;

/**
 * Unchecked exception thrown to indicate that JIRA authorization was failed.
 *
 * Created by hamitay on 8/16/17.
 */
public class JiraAuthorizationException extends IntegrationRuntimeException {

  public JiraAuthorizationException(String component, String message) {
    super(component, message);
  }

  public JiraAuthorizationException(String component, String message, Throwable cause) {
    super(component, message, cause);
  }

  public JiraAuthorizationException(String component, String message, String solution) {
    super(component, message, solution);
  }
}
