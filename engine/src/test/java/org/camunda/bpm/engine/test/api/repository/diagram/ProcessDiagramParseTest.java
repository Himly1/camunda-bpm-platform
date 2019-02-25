/*
 * Copyright © 2013 - 2019 camunda services GmbH and various authors (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine.test.api.repository.diagram;

import org.camunda.bpm.engine.impl.bpmn.diagram.ProcessDiagramLayoutFactory;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.repository.DiagramLayout;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.util.ProvidedProcessEngineRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Nikola Koevski
 */
public class ProcessDiagramParseTest {

  private static final String resourcePath = "src/test/resources/org/camunda/bpm/engine/test/api/repository/diagram/testXxeParsingIsDisabled";

  @Rule
  public ProcessEngineRule engineRule = new ProvidedProcessEngineRule();
  protected ProcessEngineConfigurationImpl processEngineConfiguration;

  boolean xxeProcessingValue;

  @Before
  public void setUp() {
    processEngineConfiguration = engineRule.getProcessEngineConfiguration();
    xxeProcessingValue = processEngineConfiguration.isEnableXxeProcessing();
    processEngineConfiguration.setEnableXxeProcessing(false);
  }

  @After
  public void tearDown() {
    processEngineConfiguration.setEnableXxeProcessing(xxeProcessingValue);
  }

  @Test
  public void testXxeParsingIsDisabled() {
    // assume that XXE Processing is disabled in the configuration (default)
    assertThat("XXE Processing should be disabled for secure engine operation!",
      engineRule.getProcessEngineConfiguration().isEnableXxeProcessing(),
      is(false)
    );

    try {
      final InputStream bpmnXmlStream = new FileInputStream(
        resourcePath + ".bpmn20.xml");
      final InputStream imageStream = new FileInputStream(
        resourcePath + ".png");

      assertNotNull(bpmnXmlStream);

      // when we run this in the ProcessEngine context
      DiagramLayout processDiagramLayout = engineRule.getProcessEngineConfiguration()
        .getCommandExecutorTxRequired()
        .execute(new Command<DiagramLayout>() {
          @Override
          public DiagramLayout execute(CommandContext commandContext) {
            return new ProcessDiagramLayoutFactory().getProcessDiagramLayout(bpmnXmlStream, imageStream);
          }
        });
      fail("The test model contains a DOCTYPE declaration! The test should fail.");
    } catch (FileNotFoundException ex) {
      fail("The test BPMN model file is missing. " + ex.getMessage());
    } catch (Exception e) {
      // then
      assertThat(e.getMessage(), containsString("Error while parsing BPMN model"));
      assertThat(e.getCause().getMessage(), containsString("DOCTYPE is disallowed"));
    }
  }
}
