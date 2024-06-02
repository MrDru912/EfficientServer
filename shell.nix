let
  sources = import ./npins;
  pkgs = import sources.nixpkgs {
    overlays = [
      (self: super: {
        graalvm21 = super.stdenv.mkDerivation {
          pname = "graalvm-java21";
          version = "21.0.2";
          src = super.fetchurl {
            url = "https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-21.0.2/graalvm-community-jdk-21.0.2_linux-x64_bin.tar.gz";
            sha256 = "b048069aaa3a99b84f5b957b162cc181a32a4330cbc35402766363c5be76ae48";
          };
          nativeBuildInputs = [ super.makeWrapper ];

          installPhase = ''
            mkdir -p $out
            tar -xzf $src --strip-components=1 -C $out
            for bin in $out/bin/*; do
              target=$out/bin/$(basename $bin)
              if [ ! -e $target ]; then
                ln -s $bin $target
              fi
            done
          '';

          meta = {
            description = "GraalVM Java 21";
            homepage = "https://github.com/graalvm/graalvm-ce-builds/releases";
            license = with super.lib.licenses; [ mit ];
            maintainers = with super.lib.maintainers; [ self ];
          };
        };
      })
    ];
  };
in
with pkgs;
mkShell {
  packages = [
    pkg-config
    graalvm21
    maven
    protobuf
  ];
}
