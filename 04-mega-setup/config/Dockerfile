# Production controller image. Built by CI, tagged <lts>-<yyyymmdd>-<sha>, scanned, pushed to ECR.
ARG JENKINS_VERSION=lts-jdk21
FROM jenkins/jenkins:${JENKINS_VERSION}

ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"

COPY --chown=jenkins:jenkins plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt --verbose \
 && rm -rf /var/jenkins_home/.cache

# Plugins live in the image (/usr/share/jenkins/ref/plugins) and are copied to JENKINS_HOME on start.
# Helm values set controller.installPlugins: false so nothing is downloaded at runtime.
