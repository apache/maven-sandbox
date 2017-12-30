#!/bin/bash

for tag in $(git tag); do
    commit_sha=$(git rev-list -n1 "refs/tags/${tag}")
    commit_message=$(git show --quiet --pretty="format:%B" "${commit_sha}")
    commit_date=$(git show --quiet --pretty="format:%aI" "${commit_sha}")
    tree_sha=$(git show --quiet --pretty="format:%T" "${commit_sha}")

    # git show --quiet ${commit_sha}

    # checking if the tag is reachable from master so the script could be ran multiple times.
    if $( git merge-base --is-ancestor "${tag}" master ) ; then
        echo "${tag} is reachable from master. Skipping."
        continue;
    fi

    if [[ ! ${commit_message} =~ git-svn ]] ; then
        echo -e "\e[1m${tag} does not point to a SVN commit and will not be modified\e[0m"
        continue
    fi

    candidate_commit_sha=$(git rev-list -n1 --grep "\[maven-release-plugin\] prepare release ${tag}$" master);
    if [ -z ${candidate_commit_sha} ] ; then
        echo -e "\e[1m${tag} message not as expected from the release plug-in. Skipping.\e[0m"
        continue
    fi

    candidate_tree_sha=$(git show --quiet --pretty="format:%T" "${candidate_commit_sha}")

    if [[ ${tree_sha} != ${candidate_tree_sha} ]] ; then
        echo -e "\e[1m${tag} - the commit created by the release plug-in ${candidate_tree_sha} does not have the same files as the tag ${tree_sha}. Skipping.\e[0m"
        continue
    fi

    echo -e "ready to change tag ${tag}\t${commit_date} from ${commit_sha} to ${candidate_commit_sha}"
    # This will change the author to the current git user but will keep original tag author date
    # The release date and author are preserved in the release commit.
    GIT_COMMITTER_DATE="${commit_date}" git tag -f -a -m "${tag} reworked after migration from aggregate svn to split git" "${tag}" "${candidate_commit_sha}" > /dev/null
done
