void call() {
    sh "echo Removing publish-users-job..."
    sh "oc delete job publish-users-job -n $NAMESPACE || :"
}

return this;
