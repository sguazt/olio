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
require 'spec/runner/formatter/base_formatter'

module Spec
  module Runner
    module Formatter
      # Baseclass for text-based formatters. Can in fact be used for
      # non-text based ones too - just ignore the +output+ constructor
      # argument.
      class BaseTextFormatter < BaseFormatter
        attr_reader :output, :pending_examples
        # Creates a new instance that will write to +where+. If +where+ is a
        # String, output will be written to the File with that name, otherwise
        # +where+ is exected to be an IO (or an object that responds to #puts and #write).
        def initialize(options, where)
          super
          if where.is_a?(String)
            FileUtils.mkdir_p(File.dirname(where))
            @output = File.open(where, 'w')
          else
            @output = where
          end
          @pending_examples = []
        end
        
        def example_pending(example, message, pending_caller)
          @pending_examples << [example.full_description, message, pending_caller]
        end
        
        def dump_failure(counter, failure)
          @output.puts
          @output.puts "#{counter.to_s})"
          @output.puts colourise("#{failure.header}\n#{failure.exception.message}", failure)
          @output.puts format_backtrace(failure.exception.backtrace)
          @output.flush
        end
        
        def colourise(s, failure)
          if(failure.expectation_not_met?)
            red(s)
          elsif(failure.pending_fixed?)
            blue(s)
          else
            magenta(s)
          end
        end
      
        def dump_summary(duration, example_count, failure_count, pending_count)
          return if dry_run?
          @output.puts
          @output.puts "Finished in #{duration} seconds"
          @output.puts

          summary = "#{example_count} example#{'s' unless example_count == 1}, #{failure_count} failure#{'s' unless failure_count == 1}"
          summary << ", #{pending_count} pending" if pending_count > 0  

          if failure_count == 0
            if pending_count > 0
              @output.puts yellow(summary)
            else
              @output.puts green(summary)
            end
          else
            @output.puts red(summary)
          end
          @output.flush
        end

        def dump_pending
          unless @pending_examples.empty?
            @output.puts
            @output.puts "Pending:"
            @pending_examples.each do |pending_example|
              @output.puts "\n#{pending_example[0]} (#{pending_example[1]})"
              @output.puts "#{pending_example[2]}\n"
            end
          end
          @output.flush
        end
        
        def close
          if IO === @output && @output != $stdout
            @output.close 
          end
        end
        
        def format_backtrace(backtrace)
          return "" if backtrace.nil?
          backtrace.map { |line| backtrace_line(line) }.join("\n")
        end
      
      protected

        def colour?
          @options.colour ? true : false
        end

        def dry_run?
          @options.dry_run ? true : false
        end
        
        def backtrace_line(line)
          line.sub(/\A([^:]+:\d+)$/, '\\1:')
        end

        def colour(text, colour_code)
          return text unless colour? && output_to_tty?
          "#{colour_code}#{text}\e[0m"
        end

        def output_to_tty?
          begin
            @output.tty? || ENV.has_key?("AUTOTEST")
          rescue NoMethodError
            false
          end
        end
        
        def green(text); colour(text, "\e[32m"); end
        def red(text); colour(text, "\e[31m"); end
        def magenta(text); colour(text, "\e[35m"); end
        def yellow(text); colour(text, "\e[33m"); end
        def blue(text); colour(text, "\e[34m"); end
        
      end
    end
  end
end
