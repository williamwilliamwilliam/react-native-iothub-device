
Pod::Spec.new do |s|
  s.name         = "RNReactNativeIothubDevice"
  s.version      = "1.0.0"
  s.summary      = "RNReactNativeIothubDevice"
  s.description  = <<-DESC
                  RNReactNativeIothubDevice
                   DESC
  s.homepage     = ""
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "author@domain.cn" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/author/RNReactNativeIothubDevice.git", :tag => "master" }
  s.source_files  = "RNReactNativeIothubDevice/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  #s.dependency "others"

end

  