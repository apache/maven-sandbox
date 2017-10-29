# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# This script is used to migrate modules from SVN to Git,
# or more precisely to split a svn2git repo to a module repo
#!/bin/sh -e

# svn2git repo to be split
svn2git_repo='maven-sandbox'

# prefix for module
prefix='dist-tools/dist-tool-plugin/'
repo_name='maven-dist-tool'
artifactId='maven-dist-tool'

git_src_location="svn2git/${svn2git_repo}"

if [ ! -d ${git_src_location} ]; then
    # clone svn2git repo
    echo "Cloning source git-svn ${svn2git_repo} ..."
    git clone https://github.com/apache/${svn2git_repo}.git ${git_src_location}
    #  ensure we don't accidentally overwrite the source repository
    chmod ugo-w ${git_src_location}
    echo "Done!"
fi

echo "Filtering ${prefix} to ${repo_name}..."
if [ ! -d ${repo_name}/.git ]; then
    # create the initial repo
    git clone --no-hardlinks ${git_src_location} ${repo_name}
    pushd ${repo_name}

    # make sure we don't push to the incorrect repo and also remove make sure
    # we don't keep references to the remote repo
    git remote rm origin

    # rename trunk to master
    git branch -m trunk master

    # Remove everything except the path belonging to the module
    git filter-branch --subdirectory-filter ${prefix}

    # remove unrelated tags
    for tag in $(git tag); do
        if [[ $tag != ${artifactId}* ]]; then
            git tag -d ${tag} > /dev/null
        fi
    done

    # cleanup and compaction
    git for-each-ref --format="%(refname)" refs/original/ | xargs -n1 git update-ref -d
    git reflog expire --expire=now --all
    git repack -Ad
    git gc --aggressive --prune=now

    git remote add origin https://gitbox.apache.org/repos/asf/${repo_name}.git

    popd
    echo "Complete!"
else
    echo "Already converted"
fi

echo "Launch 'git push --set-upstream origin master' once ready"
