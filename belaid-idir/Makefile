SHELL = /bin/sh
RM = rm -f
JAVAC = javac
JAVACFLAGS = -g -Xlint -Xlint:-serial -deprecation

DIST = projet-protint

compile = $(JAVAC) $(JAVACFLAGS)

srcdir = src
dirs = $(addprefix $(srcdir)/,common server client)
destdir = classes

.PHONY: all dist clean mrproper

all:
	mkdir -p $(destdir)
	$(compile) -d $(destdir) $(foreach dir,$(dirs),$(dir)/*.java)

dist: mrproper
	tar -cvzf ../$(DIST).tgz -C .. petitesAnnonces

clean:
	$(RM) -R $(destdir)

mrproper: clean
	$(RM) *~ \#*\# .\#*
