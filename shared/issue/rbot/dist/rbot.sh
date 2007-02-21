#!/bin/sh

dir=`pwd`

export JIRA_CONF_DIR=$dir

(
  cd bin
  
  exec ruby -I $dir/lib/site_ruby/1.8 rbot $dir $dir/share/rbot
)
