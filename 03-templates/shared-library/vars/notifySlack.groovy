// notifySlack('SUCCESS') — notify only on failures and on recovery to avoid noise.
def call(String status, String channel = '#ci') {
  def previous = currentBuild.previousBuild?.result
  def recovered = status == 'SUCCESS' && previous && previous != 'SUCCESS'
  if (status == 'SUCCESS' && !recovered) { return }

  def color = status == 'SUCCESS' ? 'good' : (status == 'UNSTABLE' ? 'warning' : 'danger')
  def label = recovered ? 'FIXED' : status
  slackSend(channel: channel, color: color,
            message: "${label}: ${env.JOB_NAME} #${env.BUILD_NUMBER} (<${env.BUILD_URL}|open>)")
}
