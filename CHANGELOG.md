# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres poorly to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- New standard getters based implementation.

### Changed
- Moved original implementation to `legacy` package.

### Fixed
- Fix unchecked casts in generic value builders.

## [0.2.10] - 2019-12-28
### Changed
- Use com.google.code.findbugs:annotations instead of com.google.code.findbugs:jsr305.

## [0.2.9] - 2019-12-10
### Added
- Support defining value classes as inner classes of interfaces and enums.

## [0.2.8] - 2019-10-14
### Security
- Update `jackson-databind`.

## [0.2.7] - 2019-07-22
### Security
- Update `jackson-databind` due to vulnerability.

## [0.2.6] - 2019-06-03
### Security
- Update dependencies.

## [0.2.5] - 2019-05-08
### Added
- Builder annotation copying.

## [0.2.4] - 2019-04-24
### Added
- Support Java 11.

## [0.2.3] - 2019-04-12
### Added
- Add annotation copying.

## [0.2.2] - 2019-03-14
### Added
- Support for values without a builder class.

### Changed
- Allow overriding `toString`, `equals` and `hashCode` methods.
- Default `toString` no longer wraps class name in double quotes.

## [0.2.1] - 2019-03-08
### Changed
- Move `jackson-databind` to compile scope.

## [0.2.0] - 2019-02-03
### Changed
- Remove dependency to `autovalue`. Almost 1:1 same classes are generated.

## [0.1.6] - 2018-11-25
### Added
- Support generic classes.

### Changed
- Move `autovalue` dependency to compile scope.

## [0.1.5] - 2018-10-18
### Security
- Update `jackson-databind` due to vulnerability.

## [0.1.3], [0.1.4] - 2019-04-05

## [0.1.2] - 2018-04-03
### Added
- Support for `@JsonIgnoreProperties`.

### Fixed
- Fix builder class downcasting.

## [0.1.1] - 2018-03-27

## [0.1.0] - 2018-03-27

Initial release.