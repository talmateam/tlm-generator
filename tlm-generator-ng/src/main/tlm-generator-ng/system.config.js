(function (global) {

    // map tells the System loader where to look for things
    
    var map = {
      'app': './build/',
      'rxjs': './build/lib/rxjs',
      '@angular': './build/lib/@angular',      
    };
  
    // packages tells the System loader how to load when no filename and/or no extension
    var packages = {
      'app': { main: 'main.js', defaultExtension: 'js' },
      'rxjs': { defaultExtension: 'js' },
      
      
    };
  
    [
      'common',
      'compiler',
      'core',
      'forms',
      'platform-browser',
      'platform-browser-0dynamic'
    ].forEach(function (pkgName) {
      packages['@angular/' + pkgName] = { main: 'bundles/' +  pkgName + '.umd.js', defaultExtension: 'js' };
    });
  
    var config = {
      map: map,
      packages: packages
    };
  
    // filterSystemConfig - index.html's chance to modify config before we register it.
    if (global.filterSystemConfig) {
      global.filterSystemConfig(config);
    }
  
    System.config(config);
  
  })(this);
