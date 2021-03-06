#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
module Spec
  module Story
    module Runner
      class StoryRunner
        def self.current_story_runner
          @current_story_runner
        end

        def self.current_story_runner=(current_story_runner)
          @current_story_runner = current_story_runner
        end

        def self.scenario_from_current_story(scenario_name)
          current_story_runner.scenario_from_current_story(scenario_name)
        end
        
        attr_accessor :stories, :scenarios, :current_story
        
        def initialize(scenario_runner, world_creator = World)
          StoryRunner.current_story_runner = self
          @scenario_runner = scenario_runner
          @world_creator = world_creator
          @stories = []
          @scenarios_by_story = {}
          @scenarios = []
          @listeners = []
        end
        
        def Story(title, narrative, params = {}, &body)
          story = Story.new(title, narrative, params, &body)
          @stories << story
          
          # collect scenarios
          collector = ScenarioCollector.new(story)
          story.run_in(collector)
          @scenarios += collector.scenarios
          @scenarios_by_story[story.title] = collector.scenarios
        end
        
        def run_stories
          return if @stories.empty?
          @listeners.each { |l| l.run_started(scenarios.size) }
          success = true
          @stories.each do |story|
            story.assign_steps_to(World)
            @current_story = story
            @listeners.each { |l| l.story_started(story.title, story.narrative) }
            scenarios = @scenarios_by_story[story.title]
            scenarios.each do |scenario|
              type = story[:type] || Object
              args = story[:args] || []
              world = @world_creator.create(type, *args)
              success = success & @scenario_runner.run(scenario, world)
            end
            @listeners.each { |l| l.story_ended(story.title, story.narrative) }
            World.step_mother.clear
          end
          unique_steps = (World.step_names.collect {|n| Regexp === n ? n.source : n.to_s}).uniq.sort
          @listeners.each { |l| l.collected_steps(unique_steps) }
          @listeners.each { |l| l.run_ended }
          return success
        end
        
        def add_listener(listener)
          @listeners << listener
        end
        
        def scenario_from_current_story(scenario_name)
          @scenarios_by_story[@current_story.title].find {|s| s.name == scenario_name }
        end
      end
    end
  end
end
