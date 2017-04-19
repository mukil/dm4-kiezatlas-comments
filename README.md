
DeepaMehta 4 Kiezatlas -  Comments
==================================

This module is a DeepaMehta 4 plugin and it introduces 

*  a type definition of a "Comment" and
*  an OSGi-Service for other modules to create and receive comments on all kinds of topics.

It is generic in its design and thus can be used with many different topic types. It is mainly used by the [Kiezatlas Website](https://github.com/mukil/dm4-kiezatlas-website) and [Kiezatlas API](http://github.com/mukil/dm4-kiezatlas-famportal) plugins to receive and handle messages on places.

Comments created via this PluginService are assigned to the "Comments" (SharingMode.Collaborative, publicly not-readaable) workspace by default (disregarding any dm4-workspace-cookie send with the request).


### Usage & Development

See and compare to the section on [usage and development in this README](https://github.com/mukil/dm4-kiezatlas-website#usage--development).


License
-------

Thise source code is licensed under the GNU GPL 3.0. It comes with no warranty.


Version History
---------------

**1.0** -- Feb 12, 2017

- Basically functional
- Introduces a dedicated "Comments" workspace
- Introduces topic type defintion "Comment"

Author: Malte Reißig (2017)
