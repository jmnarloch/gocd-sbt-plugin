/**
 * Copyright (c) 2015 the original author or authors
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
package io.jmnarloch.cd.go.plugin.sbt;

/**
 * The SBT command.
 *
 * @author Jakub Narloch
 */
public class Sbt {

    /**
     * The SBT command.
     *
     * @return the sbt command
     */
    public static Command sbt() {
        return SbtCommand.INSTANCE;
    }

    public interface Command {

        String unix();

        String windows();
    }

    private static class SbtCommand implements Command {

        private static final String SBT_UNIX = "./sbt";

        private static final String SBT_WINDOWS = "./sbt.bat";

        private static final SbtCommand INSTANCE = new SbtCommand();

        @Override
        public String unix() {
            return SBT_UNIX;
        }

        @Override
        public String windows() {
            return SBT_WINDOWS;
        }
    }
}
