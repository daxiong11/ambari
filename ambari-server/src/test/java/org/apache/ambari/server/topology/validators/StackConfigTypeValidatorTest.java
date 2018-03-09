/*
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

package org.apache.ambari.server.topology.validators;

import static org.easymock.EasyMock.expect;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.ambari.server.controller.internal.Stack;
import org.apache.ambari.server.topology.Blueprint;
import org.apache.ambari.server.topology.ClusterTopology;
import org.apache.ambari.server.topology.Configuration;
import org.apache.ambari.server.topology.InvalidTopologyException;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class StackConfigTypeValidatorTest extends EasyMockSupport {

  @Rule
  public EasyMockRule mocks = new EasyMockRule(this);

  @Mock
  private Configuration clusterConfigurationMock;

  @Mock
  private Configuration stackConfigurationMock;

  @Mock
  private Blueprint blueprintMock;

  @Mock
  private Stack stackMock;

  @Mock
  private ClusterTopology clusterTopologyMock;

  @TestSubject
  private StackConfigTypeValidator stackConfigTypeValidator = new StackConfigTypeValidator();

  @Before
  public void before() {
    expect(clusterTopologyMock.getConfiguration()).andReturn(clusterConfigurationMock).anyTimes();
    expect(clusterTopologyMock.getBlueprint()).andReturn(blueprintMock).anyTimes();

    expect(clusterTopologyMock.getStack()).andReturn(stackMock).anyTimes();
  }

  @After
  public void after() {
    resetAll();
  }


  @Test(expected = InvalidTopologyException.class) // THEN
  public void rejectInvalidType() throws Exception {
    // GIVEN
    expect(stackMock.getConfiguration()).andReturn(stackConfigurationMock);
    expect(stackConfigurationMock.getAllConfigTypes()).andReturn(Arrays.asList("core-site", "yarn-site"));
    expect(clusterConfigurationMock.getAllConfigTypes()).andReturn(Collections.singleton("invalid-site"));
    replayAll();

    // WHEN
    stackConfigTypeValidator.validate(clusterTopologyMock);
  }

  @Test
  public void allowEmptyConfig() throws Exception {
    // GIVEN
    expect(stackMock.getConfiguration()).andReturn(stackConfigurationMock);
    expect(stackConfigurationMock.getAllConfigTypes()).andReturn(Arrays.asList("core-site", "yarn-site"));
    expect(clusterConfigurationMock.getAllConfigTypes()).andReturn(Collections.emptySet());
    replayAll();

    // WHEN
    stackConfigTypeValidator.validate(clusterTopologyMock);

    // THEN
    // no exception is thrown
  }

  @Test(expected = InvalidTopologyException.class) // THEN
  public void rejectMultipleInvalidTypes() throws Exception {
    // GIVEN
    expect(stackMock.getConfiguration()).andReturn(stackConfigurationMock);
    expect(stackConfigurationMock.getAllConfigTypes()).andReturn(Arrays.asList("core-site", "yarn-site"));
    expect(clusterConfigurationMock.getAllConfigTypes()).andReturn(Arrays.asList("invalid-site-1", "invalid-default"));
    replayAll();

    // WHEN
    stackConfigTypeValidator.validate(clusterTopologyMock);
  }

  @Test
  public void allowValidTypes() throws InvalidTopologyException {
    // GIVEN
    List<String> configTypes = Arrays.asList("core-site", "yarn-site");
    expect(stackMock.getConfiguration()).andReturn(stackConfigurationMock);
    expect(stackConfigurationMock.getAllConfigTypes()).andReturn(configTypes);
    expect(clusterConfigurationMock.getAllConfigTypes()).andReturn(configTypes);
    replayAll();

    // WHEN
    stackConfigTypeValidator.validate(clusterTopologyMock);

    // THEN
    // no exception is thrown
  }
}
