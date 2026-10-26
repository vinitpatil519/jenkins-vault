# Jenkins controller with plugins baked in (reproducible, instant rollback by image tag)
FROM jenkins/jenkins:lts-jdk21

COPY --chown=jenkins:jenkins plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt

# JCasC files are mounted at runtime (see compose.yaml) so edits need only a reload
