# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres poorly to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.4.2] - 2024-08-31
### Changed
- Bump fi.jubic:easyparent from 0.1.12 to 0.1.13.
- Bump com.fasterxml.jackson.core:jackson-annotations from 2.17.1 to 2.17.2.
- Bump com.fasterxml.jackson.core:jackson-databind from 2.17.1 to 2.17.2.

## [1.4.1] - 2024-05-06
### Changed
- Bump fi.jubic:easyparent from 0.1.10 to 0.1.12.
- Bump com.fasterxml.jackson.core:jackson-annotations from 2.16.0 to 2.17.1.
- Bump com.fasterxml.jackson.core:jackson-databind from 2.16.0 to 2.17.1.

## [1.4.0] - 2023-12-11
### Added
- Java 21 tests.

### Removed
- Removed deprecated legacy annotation support.

## [1.3.8] - 2023-03-09
### Security
- Update parent and dependencies.

## [1.3.7] - 2022-06-19
### Security
- Update parent and jackson-databind

## [1.3.6] - 2021-12-13
### Fixed
- Collection property default value behavior.

## [1.3.5] - 2021-12-12
### Added
- `Collections::unmodifiable*` wraps for `Set` and `Map` instances.

### Fixed
- Initial `Collections::unmodifiable*` logic to account for null values.

## [1.3.4] - 2021-12-10
### Changed
- Wrap `List` instances in `Collections::unmodifiableList` to appease spotbugs.

## [1.3.3] - 2021-12-10
### Added
- Full Java 17 support.

### Security
- Update dependencies.

## [1.3.2] - 2021-04-13
### Security
- Update dependencies.
- Suppress CVE-2020-8908 in dependency check.

## [1.3.1] - 2020-12-23
### Fixed
- Fix missing check and defaults for primitive properties. Previously defaults were ignored for primitive values and the "reasonable defaults" were used instead for unset properties.

## [1.3.0] - 2020-06-17
### Added
- Support for Arrays. Arrays are omitted from `toString`, treated correctly on equals and copied in getters and constructors.

## [1.2.0] - 2020-06-10
### Added
- Add support for `Optional` properties with nullable setters and `Optional` wrapped getters.

## [1.1.0] - 2020-02-04
### Added
- Add `defaults` function to generated builders. Default values defined in `defaults` method
are applied to values de-serialized using Jackson.

## [1.0.0] - 2020-02-03
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
