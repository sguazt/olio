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
class RenderSpecController < ApplicationController
  set_view_path File.join(File.dirname(__FILE__), "..", "views")
  
  def some_action
    respond_to do |format|
      format.html
      format.js
    end
  end
  
  def action_which_renders_template_from_other_controller
    render :template => 'controller_spec/action_with_template'
  end
  
  def text_action
    render :text => "this is the text for this action"
  end
  
  def action_with_partial
    render :partial => "a_partial"
  end
  
  def action_that_renders_nothing
    render :nothing => true
  end
end
