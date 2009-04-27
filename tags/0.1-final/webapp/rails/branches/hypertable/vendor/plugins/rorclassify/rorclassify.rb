
require 'erb'
require 'rubygems'

# This script assumes that the :application and :group_num will already be set.

# Set the defaults
set :deploy_to, "/work/Fall07/#{application}"
set :domain, "#{application}.rorclass.org"
set :use_sudo, false
set :repository, "http://svn.rorclass.org/repos/#{application}/trunk"
set :scm_username, 'sdjlaksdsa'
ssh_options[:paranoid] = false

# Set the default servers
role :app, domain
role :web, domain
role :db, domain, :primary => true

# Database related
set :db_adapter, 'mysql'
set :db_user, 'root'
set :db_password, ''

# Apache configuration
set :apache_proxy_address, 'localhost'
set :apache_proxy_port, 8000 + group_num * 2
set :apache_proxy_servers, 2
set :apache_server_name, domain
set :apache_ssl_enabled, false
set :apache_server_aliases_array, []

def get_machine
  return $_machine if defined? $_machine
  $_machine = nil
  run("uname -n") do |ssh, sid, data|
    $_machine = data.chomp
  end
  return $_machine
end

# All the required setup tasks
after 'deploy:setup', 'mongrel:setup', 'apache:setup', 'mysql:setup'

namespace :deploy do
  desc "Override the default restart and execute mongel:restart task. See mongrel:restart"
  task :restart, :roles => :app, :except => { :no_release => true } do
    mongrel.restart
  end

  desc "Override the default start and execute mongrel:start"
  task :start, :roles => :app do
    mongrel.start
  end

  desc "Override the default stop and execute mongrel:stop"
  task :stop, :roles => :app do
    mongrel.stop
  end
end

Dir[File.join(File.dirname(__FILE__), 'lib/*.rb')].each { |f| eval File.read(f) }
