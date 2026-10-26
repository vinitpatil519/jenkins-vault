package org.acme.ci

// Classes in src/ must be Serializable: pipeline state (CPS) is written to disk between steps.
class Semver implements Serializable {
  final int major
  final int minor
  final int patch

  Semver(String version) {
    def parts = version.replaceFirst(/^v/, '').tokenize('.')*.toInteger()
    major = parts[0]; minor = parts[1]; patch = parts[2]
  }

  String bump(String part) {
    switch (part) {
      case 'major': return "${major + 1}.0.0"
      case 'minor': return "${major}.${minor + 1}.0"
      default:      return "${major}.${minor}.${patch + 1}"
    }
  }

  String toString() { "${major}.${minor}.${patch}" }
}
