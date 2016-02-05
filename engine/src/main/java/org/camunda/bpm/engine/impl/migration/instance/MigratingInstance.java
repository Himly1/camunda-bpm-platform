/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine.impl.migration.instance;

import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;

/**
 * @author Thorben Lindhauer
 *
 */
public interface MigratingInstance {

  /**
   * Detach this instance's state from the execution tree
   */
  void detachState();

  /**
   * Restore this instance's state to the execution tree.
   * Restoration should restore the state that was detached
   * before (i.e. the source process definition state)
   * @param newScopeExecution
   */
  void attachState(ExecutionEntity newScopeExecution);

  /**
   * Migrate state from the source process definition
   * to the target process definition.
   */
  void migrateState();

  /**
   * Migrate instances that are aggregated by this instance
   * (e.g. an activity instance aggregates task instances).
   */
  void migrateDependentEntities();
}