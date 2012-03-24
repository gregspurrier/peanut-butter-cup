# Peanut Butter Cup
Clojure doc strings are the chocolate. Midje facts are peanut butter. Each good on their own, but even betten better when combined.

Peanut Butter Cup will produce HTML documentation for projects combining the doc string and argument lists from a function's definition with example usages from its Midje facts.

## Status
At this point `peanut-butter-cup.core/get-fn-metadata-and-facts` can take a namespace and return a seq of metadata extracted from the public vars in that namespace, updated with the corresponding facts from Midje. Generating HTML output is still to be done.

## License
Copyright (c) 2012 Greg Spurrier. Distributed under the MIT license. See LICENSE.txt for details.